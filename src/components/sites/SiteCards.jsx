import { Link } from 'react-router-dom';

export default function SiteCards({ sites }) {
  if (sites.length === 0) {
    return (
      <div className="alert alert-info">
        There are no sites available.
      </div>
    );
  }

  return (
    <div className="flex-container">
      {sites.map((site) => (
        <Link to={`/sites/${site.id}`} key={site.id}>
          <div className="card">
            <div className="card-body">
              <h5 className="card-title">{site.name}</h5>
              <p className="card-text">{site.manager}</p>
              <p className="card-text">{site.address}</p>
            </div>
          </div>
        </Link>
      ))}
    </div>
  );
}