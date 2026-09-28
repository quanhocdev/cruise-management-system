const fs = require('node:fs');
const ids=JSON.parse(fs.readFileSync('scripts/finance-demo/ids.json','utf8'));
const base='http://localhost:8080';
async function api(path,body,token,expected=200){
 const r=await fetch(base+path,{method:body?'POST':'GET',headers:{'Content-Type':'application/json',...(token?{Authorization:'Bearer '+token}:{})},body:body?JSON.stringify(body):undefined});
 const text=await r.text(); if(r.status!==expected)throw Error(path+' HTTP '+r.status+' '+text);
 return text?JSON.parse(text):null;
}
(async()=>{
 const {token}=await api('/api/auth/login',{username:'finance',password:process.env.DEMO_PASSWORD || 'admin@123'});
 const tours=await api('/api/finance/tours',null,token);if(!tours.some(t=>t.id===ids.tour))throw Error('Demo tour missing');
 const bookings=await api('/api/finance/bookings?tourId='+ids.tour,null,token); console.log('Demo bookings:',bookings.map(b=>b.bookingCode).join(', '));
 const socket = new WebSocket('ws://localhost:8080/ws-booking/websocket');
 const event=await new Promise((resolve,reject)=>{
  const timer=setTimeout(()=>{socket.close();reject(Error('WebSocket event timeout'));},15000);
  socket.onopen=()=>socket.send('CONNECT\naccept-version:1.2\nhost:localhost\n\n\0');
  socket.onerror=()=>{clearTimeout(timer);reject(Error('WebSocket failed'));};
  socket.onmessage=async e=>{try{
   const frame=String(e.data);
   if(frame.startsWith('CONNECTED')) { socket.send('SUBSCRIBE\nid:demo\ndestination:/topic/tour/'+ids.tour+'/scans\n\n\0'); await new Promise(r=>setTimeout(r,500)); await api('/api/finance/scan',{bookingCode:'DEMO-POS-001'},token); }
   if(frame.startsWith('RECEIPT'))await api('/api/finance/scan',{bookingCode:'DEMO-POS-001'},token);
   if(frame.startsWith('MESSAGE')){clearTimeout(timer);resolve(JSON.parse(frame.split('\n\n')[1].replace(/\0.*$/s,'')));}
  }catch(err){clearTimeout(timer);reject(err);}};
 });socket.close();if(event.bookingId!==-928001)throw Error('Wrong WebSocket booking');console.log('QR -> gateway -> WebSocket: PASS');
 const path='/api/finance/check-passenger/-928001/check-in-passenger';
 await api(path,{},token,400);console.log('Missing fields: rejected');
 const payload={passengerId:-928001,roomId:ids.room1,nfcCode:'DEMO-NFC-001'};
 await api(path,payload,token);
 const first=(await api('/api/finance/bookings/-928001/passengers',null,token))[0];
 if(first.status!=='CHECKED_IN'||first.nfcCardUid!==payload.nfcCode||first.roomId!==payload.roomId)throw Error('Assignment mismatch');
 await api(path,payload,token);
 const again=(await api('/api/finance/bookings/-928001/passengers',null,token))[0];
 if(first.checkedInAt!==again.checkedInAt)throw Error('Duplicate changes check-in time');
 await api(path,{...payload,nfcCode:'DEMO-NFC-002'},token,409);
 await api('/api/finance/check-passenger/-928002/check-in-passenger',{...payload,passengerId:-928002,roomId:ids.room2},token,409);
 console.log('Check-in, persisted room/NFC, idempotent repeat, overwrite/card reuse guards: PASS');
 const cards=await api('/api/finance/check-passenger/available-wristbands',null,token);
 if(cards.some(c=>c.cardUid==='DEMO-NFC-001'))throw Error('Assigned card still selectable');
 await api('/api/finance/check-passenger/-928002/check-in-passenger',{passengerId:-928002,roomId:ids.room1,nfcCode:'DEMO-NFC-002'},token,409);
 await api('/api/finance/check-passenger/-928002/check-in-passenger',{passengerId:-928002,roomId:ids.room2,nfcCode:'DOES-NOT-EXIST'},token,409);
 console.log('Assigned-card filtering, occupied room and unknown NFC guards: PASS');

 const other=await api('/api/auth/login',{username:'convenience',password:process.env.DEMO_PASSWORD || 'admin@123'});
 await api('/api/finance/scan',{bookingCode:'DEMO-POS-001'},other.token,403);console.log('Non-Finance scan: rejected');
})().catch(e=>{console.error(e.message);process.exitCode=1;});
