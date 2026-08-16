import "../../styles/operation/OperationFooter.css";

function OperationFooter() {
  return (
    <footer className="operation-footer">
      <span>© {new Date().getFullYear()} Cruise Management System</span>

      <span>Operation Management</span>
    </footer>
  );
}

export default OperationFooter;
