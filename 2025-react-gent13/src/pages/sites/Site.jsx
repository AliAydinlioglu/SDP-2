import { useParams, Outlet } from 'react-router-dom';
import useSWR from 'swr';
import { getAll } from '../../api';
import SiteDetail from '../../components/sites/SiteDetail';
import MachineTabelSmall from '../../components/machines/MachineTabelSmall';

export default function Site() {
  const { id } = useParams();
  const idNum = Number(id);

  const { data: sites = [], isLoading: sitesLoading, error: sitesError } = useSWR('/sites', getAll);
  const { data: machines = [], isLoading: machinesLoading, error: machinesError } = useSWR('/machines', getAll);

  if (sitesLoading || machinesLoading) {
    return <div className="alert alert-info">Loading site and machine data...</div>;
  }

  if (sitesError || machinesError) {
    return <div className="alert alert-danger">Failed to load site or machine data.</div>;
  }

  const site = sites.find((s) => s.id === idNum);
  const filteredMachines = machines.filter((m) => m.site_id === idNum);

  if (!site) {
    return <div className="alert alert-warning">Site not found.</div>;
  }

  return (
    <div className="site-container">
      <div className="site-details"><SiteDetail site={site} /></div>
      <div className="machine-list"><MachineTabelSmall machines={filteredMachines} /></div>
      <div className="machine-details"><Outlet context={{ machines: filteredMachines }} /></div>
    </div>
  );
}