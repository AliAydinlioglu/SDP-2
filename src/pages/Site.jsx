import { useParams } from 'react-router';
import {SITE_DATA, MACHINE_DATA }from '../api/mock_data';
import SiteDetail from '../components/Site/SiteDetail';
import MachineTabel from '../components/Machine/MachineTabel';

export default function Site(){
  const { id } = useParams();
  const idNum = Number(id);

  const site = SITE_DATA.find((s) => s.id === idNum);
  const machines = MACHINE_DATA.filter((m) => m.site_id === idNum);
  console.log(site);
  
  return (
    <div className="container">
      <SiteDetail site={site} />
      <MachineTabel machines={machines} />
    </div>
  );
}