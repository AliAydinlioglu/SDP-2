import { useParams } from 'react-router';
import SITE_DATA from '../api/mock_data';
import SiteDetail from '../components/Site/SiteDetail';
export default function Site(){
  const { id } = useParams();
  const idNum = Number(id);

  const site = SITE_DATA.find((s) => s.id === idNum);
  console.log(site);
  
  return (
    <SiteDetail site={site}/>
  );
}