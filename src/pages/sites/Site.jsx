import { useParams } from 'react-router';
import {SITE_DATA, MACHINE_DATA }from '../../api/mock_data';
import SiteDetail from '../../components/sites/SiteDetail';
import MachineTabel from '../../components/machines/MachineTabel';

export default function Site(){
  const { id } = useParams();
  const idNum = Number(id);

  const site = SITE_DATA.find((s) => s.id === idNum);
  const machines = MACHINE_DATA.filter((m) => m.site_id === idNum);
  console.log(site);
  console.log(machines);
  
  return (
    <div className="container">
      <SiteDetail site={site} />
      <MachineTabel machines={machines} />
    </div>
  );
}