import "../../styles/onboard/OnboardFooter.css";

function OnboardFooter() {
  return (
    <footer className="onboard-footer">
      <span>© {new Date().getFullYear()} Cruise Management System</span>

      <div className="onboard-footer-links">
        <a href="#help" className="onboard-footer-link">
          Trợ giúp
        </a>
        <a href="#privacy" className="onboard-footer-link">
          Bảo mật
        </a>
      </div>
    </footer>
  );
}

export default OnboardFooter;
