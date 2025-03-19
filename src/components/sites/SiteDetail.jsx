import { MACHINE_DATA } from '../../api/mock_data';
import { Link } from 'react-router-dom';
import { IoTrashOutline, IoPencilOutline } from 'react-icons/io5';

export default function SiteDetail({ site }) {
  const machineCount = MACHINE_DATA.filter((machine) => machine.site_id === site.id).length;

  return (
    <div>
      <h1>Site Detail</h1>
      <p>Name: {site.naam}</p>
      <p>Manager: {site.verantw_id}</p>
      <p>Number of Machines: {machineCount}</p>
      <Link to={`/sites/edit/${site.id}`} className='btn btn-light'>
        <IoPencilOutline />
      </Link>
    </div>
  );
}