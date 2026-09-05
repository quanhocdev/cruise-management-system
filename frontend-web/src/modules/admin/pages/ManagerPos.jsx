import { useEffect, useMemo, useState } from "react";
import { Alert, Badge, Button, Card, Form, Modal, Spinner, Table } from "react-bootstrap";
import QRCode from "qrcode";
import {
  assignVoyage, issueCredential, listCredentials, listTerminals,
  listVoyagePassengers, registerTerminal, revokeCredential,
} from "../services/posService";
import "../styles/ManagerPos.css";

const errorText = (error) => error.response?.data?.message || "Không thể xử lý yêu cầu. Vui lòng thử lại.";

export default function ManagerPos() {
  const [terminals, setTerminals] = useState([]);
  const [selectedCode, setSelectedCode] = useState("");
  const [voyageId, setVoyageId] = useState("");
  const [passengers, setPassengers] = useState([]);
  const [credentials, setCredentials] = useState([]);
  const [passengerVoyageId, setPassengerVoyageId] = useState("");
  const [scanType, setScanType] = useState("QR");
  const [nfcUid, setNfcUid] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [registerOpen, setRegisterOpen] = useState(false);
  const [terminalForm, setTerminalForm] = useState({ code: "", name: "" });
  const [oneTimeSecret, setOneTimeSecret] = useState(null);
  const [issued, setIssued] = useState(null);
  const [qrImage, setQrImage] = useState("");

  const selectedTerminal = useMemo(
    () => terminals.find((item) => item.code === selectedCode), [terminals, selectedCode],
  );

  const refreshTerminals = async () => {
    const data = await listTerminals();
    setTerminals(data);
    if (!selectedCode && data.length) setSelectedCode(data[0].code);
  };

  useEffect(() => {
    refreshTerminals().catch((reason) => setError(errorText(reason)));
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (selectedTerminal?.assignedVoyageId) setVoyageId(selectedTerminal.assignedVoyageId);
  }, [selectedTerminal]);

  const loadVoyage = async (id = voyageId) => {
    if (!id.trim()) return setError("Vui lòng nhập Voyage ID.");
    setBusy(true); setError("");
    try {
      const [passengerData, credentialData] = await Promise.all([
        listVoyagePassengers(id.trim()), listCredentials(id.trim()),
      ]);
      setPassengers(passengerData); setCredentials(credentialData);
      if (!passengerVoyageId && passengerData.length) setPassengerVoyageId(String(passengerData[0].passengerVoyageId));
    } catch (reason) { setError(errorText(reason)); }
    finally { setBusy(false); }
  };

  const handleAssign = async () => {
    if (!selectedCode) return setError("Vui lòng chọn máy POS.");
    setBusy(true); setError(""); setSuccess("");
    try {
      await assignVoyage(selectedCode, voyageId.trim());
      await refreshTerminals(); await loadVoyage(voyageId.trim());
      setSuccess(`Đã gán chuyến cho ${selectedCode}.`);
    } catch (reason) { setError(errorText(reason)); }
    finally { setBusy(false); }
  };

  const handleRegister = async (event) => {
    event.preventDefault(); setBusy(true); setError("");
    try {
      const result = await registerTerminal({ code: terminalForm.code.trim(), name: terminalForm.name.trim() });
      setOneTimeSecret(result); setSelectedCode(result.code); await refreshTerminals();
    } catch (reason) { setError(errorText(reason)); }
    finally { setBusy(false); }
  };

  const handleIssue = async () => {
    setBusy(true); setError(""); setSuccess("");
    try {
      const result = await issueCredential({
        passengerVoyageId: Number(passengerVoyageId), scanType,
        nfcUid: scanType === "NFC" ? nfcUid.trim() : null,
      });
      setIssued(result);
      setQrImage(scanType === "QR" ? await QRCode.toDataURL(result.scannedValue, { width: 300, margin: 2 }) : "");
      await loadVoyage();
    } catch (reason) { setError(errorText(reason)); }
    finally { setBusy(false); }
  };

  const handleRevoke = async (id) => {
    if (!window.confirm("Thu hồi credential này? Thẻ/mã sẽ không thể dùng lại.")) return;
    setBusy(true); setError("");
    try { await revokeCredential(id); await loadVoyage(); setSuccess("Đã thu hồi credential."); }
    catch (reason) { setError(errorText(reason)); }
    finally { setBusy(false); }
  };

  return (
    <div className="manager-pos-page container-fluid py-4">
      <div className="pos-heading">
        <div><h2>Quản lý máy POS</h2><p>Cấp thiết bị, gán chuyến và quản lý QR/NFC nhận diện hành khách.</p></div>
        <Button onClick={() => { setRegisterOpen(true); setOneTimeSecret(null); }}>+ Cấp máy POS</Button>
      </div>
      {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}
      {success && <Alert variant="success" dismissible onClose={() => setSuccess("")}>{success}</Alert>}

      <div className="pos-grid">
        <Card><Card.Body><Card.Title>1. Gán chuyến cho thiết bị</Card.Title>
          <Form.Group className="mb-3"><Form.Label>Máy POS</Form.Label>
            <Form.Select value={selectedCode} onChange={(e) => setSelectedCode(e.target.value)}>
              <option value="">Chọn thiết bị</option>
              {terminals.map((item) => <option key={item.id} value={item.code}>{item.code} — {item.name}</option>)}
            </Form.Select>
          </Form.Group>
          <Form.Group className="mb-3"><Form.Label>Voyage ID</Form.Label>
            <Form.Control value={voyageId} onChange={(e) => setVoyageId(e.target.value)} placeholder="UUID của chuyến khởi hành" />
          </Form.Group>
          <Button disabled={busy} onClick={handleAssign}>Gán chuyến và tải hành khách</Button>
          {selectedTerminal && <div className="pos-device-note">Trạng thái: <Badge bg={selectedTerminal.active ? "success" : "secondary"}>{selectedTerminal.active ? "Hoạt động" : "Đã khóa"}</Badge></div>}
        </Card.Body></Card>

        <Card><Card.Body><Card.Title>2. Cấp credential hành khách</Card.Title>
          <Form.Group className="mb-3"><Form.Label>Hành khách</Form.Label>
            <Form.Select value={passengerVoyageId} onChange={(e) => setPassengerVoyageId(e.target.value)}>
              <option value="">Chọn hành khách đã thanh toán</option>
              {passengers.map((item) => <option key={item.passengerVoyageId} value={item.passengerVoyageId}>{item.fullName} — {item.bookingCode}</option>)}
            </Form.Select>
          </Form.Group>
          <Form.Group className="mb-3"><Form.Label>Loại</Form.Label>
            <Form.Select value={scanType} onChange={(e) => setScanType(e.target.value)}><option>QR</option><option>NFC</option></Form.Select>
          </Form.Group>
          {scanType === "NFC" && <Form.Group className="mb-3"><Form.Label>UID thẻ NFC</Form.Label>
            <Form.Control value={nfcUid} onChange={(e) => setNfcUid(e.target.value)} placeholder="Ví dụ: 04:A1:B2:C3" />
            <Form.Text>UID gồm 4, 7 hoặc 10 byte hex. Đọc UID từ điện thoại POS rồi nhập tại đây.</Form.Text>
          </Form.Group>}
          <Button disabled={busy || !passengerVoyageId} onClick={handleIssue}>Cấp {scanType}</Button>
        </Card.Body></Card>
      </div>

      <Card className="mt-4"><Card.Body><Card.Title>Credential của chuyến</Card.Title>
        {busy ? <Spinner animation="border" /> : <Table responsive hover><thead><tr><th>Hành khách</th><th>Booking</th><th>Loại</th><th>Trạng thái</th><th></th></tr></thead>
          <tbody>{credentials.map((item) => <tr key={item.id}><td>{item.fullName}</td><td>{item.bookingCode}</td><td>{item.scanType}</td><td><Badge bg={item.active ? "success" : "secondary"}>{item.active ? "Đang dùng" : "Đã thu hồi"}</Badge></td><td>{item.active && <Button size="sm" variant="outline-danger" onClick={() => handleRevoke(item.id)}>Thu hồi</Button>}</td></tr>)}</tbody></Table>}
        {!busy && !credentials.length && <p className="text-muted mb-0">Chưa có credential hoặc chưa tải chuyến.</p>}
      </Card.Body></Card>

      <Modal show={registerOpen} onHide={() => !busy && setRegisterOpen(false)} centered><Form onSubmit={handleRegister}>
        <Modal.Header closeButton><Modal.Title>Cấp máy POS</Modal.Title></Modal.Header><Modal.Body>
          {!oneTimeSecret ? <><Form.Group className="mb-3"><Form.Label>Mã máy</Form.Label><Form.Control required maxLength={60} value={terminalForm.code} onChange={(e) => setTerminalForm({ ...terminalForm, code: e.target.value })} placeholder="POS-LOBBY-01" /></Form.Group><Form.Group><Form.Label>Tên máy</Form.Label><Form.Control required maxLength={120} value={terminalForm.name} onChange={(e) => setTerminalForm({ ...terminalForm, name: e.target.value })} placeholder="Quầy lễ tân" /></Form.Group></> : <Alert variant="warning" className="mb-0"><strong>POS API KEY chỉ hiện một lần.</strong><div className="secret-box">{oneTimeSecret.secret}</div><Button size="sm" onClick={() => navigator.clipboard.writeText(oneTimeSecret.secret)}>Sao chép key</Button></Alert>}
        </Modal.Body><Modal.Footer>{!oneTimeSecret && <Button type="submit" disabled={busy}>Tạo và lấy key</Button>}<Button variant="secondary" onClick={() => setRegisterOpen(false)}>Đóng</Button></Modal.Footer>
      </Form></Modal>

      <Modal show={Boolean(issued)} onHide={() => setIssued(null)} centered><Modal.Header closeButton><Modal.Title>Credential vừa cấp</Modal.Title></Modal.Header><Modal.Body className="text-center">
        <Alert variant="warning">Giá trị này chỉ hiện một lần. Hãy in/lưu ngay.</Alert>
        {qrImage && <img className="issued-qr" src={qrImage} alt="QR nhận diện hành khách" />}
        <div className="secret-box">{issued?.scannedValue}</div>
        <Button onClick={() => navigator.clipboard.writeText(issued.scannedValue)}>Sao chép</Button>{qrImage && <Button className="ms-2" variant="outline-primary" onClick={() => window.print()}>In QR</Button>}
      </Modal.Body></Modal>
    </div>
  );
}
