import SiteCards from '../components/Site/SiteCards';

export default function DashBoard() {
  return (
    <div className="container-fluid">
      <h1>Dashboard</h1>
      <div>
        <div className="col-md-8" style={{ border: '1px solid black', overflowY: 'scroll' }}>
          <SiteCards />
        </div>
        <div className="col-md-8">
          {/* Other content can go here */}
        </div>
      </div>
    </div>
  );
}