import { Link } from 'react-router-dom';
import useSWR from 'swr';
import { getAll } from '../../api';

export default function SiteDetail({ site }) {
  const { data: machines = [], error, isLoading } = useSWR('/machines', getAll);

  if (isLoading) {
    return <div className="alert alert-info">Loading site details...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">Failed to load machines.</div>;
  }

  const machineCount = machines.filter((machine) => machine.site_id === site.id).length;

  return (
    <div>
      <h1>Site Detail</h1>
      <p>Name: {site.naam}</p>
      <p>Manager: {site.verantw_id}</p>
      <p>Number of Machines: {machineCount}</p>
      <Link to={`/sites/edit/${site.id}`} className="btn btn-light">
        Edit
      </Link>
    </div>
  );
}