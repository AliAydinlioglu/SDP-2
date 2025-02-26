import { useParams, Outlet } from 'react-router-dom';
import { SITE_DATA, MACHINE_DATA } from '../../api/mock_data';
import SiteDetail from '../../components/sites/SiteDetail';
import MachineTabel from '../../components/machines/MachineTabel';

export default function Site() {
  const { id } = useParams();
  const idNum = Number(id);

  const site = SITE_DATA.find((s) => s.id === idNum);
  const machines = MACHINE_DATA.filter((m) => m.site_id === idNum);

  return (
    <div className="site-container">
      <div className="site-details"><SiteDetail site={site} /></div>
      <div className="machine-list"><MachineTabel machines={machines} /></div>
      <div className='machine-details'><Outlet context={{ machines }} /></div>
    </div>
  );
}