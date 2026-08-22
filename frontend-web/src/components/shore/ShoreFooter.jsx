import "../../styles/shore/ShoreFooter.css";

function ShoreFooter() {
  return (
    <footer className="shore-footer">
      <span>© {new Date().getFullYear()} Cruise Management System</span>

      <span>Shore Management</span>
    </footer>
  );
}

export default ShoreFooter;
