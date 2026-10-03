import { Link } from 'react-router-dom';
import useSWR from 'swr';
import { getAll } from '../../api';

export default function SiteCards({ sites }) {
  const { data: machines = [], error, isLoading } = useSWR('/machines', getAll);

  if (isLoading) {
    return <div className="alert alert-info">Loading sites...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">Failed to load machines.</div>;
  }

  if (sites.length === 0) {
    return (
      <div className="alert alert-info">
        There are no sites available.
      </div>
    );
  }

  return (
    <div className="site-cards-container">
      {sites.map((site) => {
        const filteredMachines = machines.filter((machine) => machine.site_id === site.id);
        const machineCount = filteredMachines.length;

        return (
          <Link to={`/sites/${site.id}`} key={site.id}>
            <div className="card">
              <div className="card-body">
                <h5 className="card-title">{site.naam}</h5>
                <p className="card-text">Responsible ID: {site.verantw_id}</p>
                <p className="card-text">Number of Machines: {machineCount}</p>
              </div>
            </div>
          </Link>
        );
      })}
    </div>
  );
}