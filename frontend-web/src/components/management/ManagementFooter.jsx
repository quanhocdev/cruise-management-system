// ManagementFooter.jsx
import "../../styles/management/footer.css";

function ManagementFooter({ rightText, links }) {
  return (
    <footer className="mgmt-footer">
      <span>© {new Date().getFullYear()} Cruise Management System</span>

      {links?.length ? (
        <div className="mgmt-footer-links">
          {links.map((link) => (
            <a key={link.href} href={link.href} className="mgmt-footer-link">
              {link.label}
            </a>
          ))}
        </div>
      ) : (
        <span className="mgmt-footer-role">{rightText}</span>
      )}
    </footer>
  );
}

export default ManagementFooter;
