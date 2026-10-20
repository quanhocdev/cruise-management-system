// ManagementFooter.jsx
import "../../styles/management/footer.css";

function ManagementFooter({ rightText }) {
  return (
    <footer className="mgmt-footer">
      <span>© {new Date().getFullYear()} Cruise Management System</span>
      <span>{rightText}</span>
    </footer>
  );
}

export default ManagementFooter;
