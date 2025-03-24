import { useParams } from 'react-router-dom';
import { MACHINE_DATA } from '../../api/mock_data';
import { IoPencilOutline } from 'react-icons/io5';
import { Link } from 'react-router-dom';

const MachineDetailBig = () => {
  const { id } = useParams();
  const idAsNumber = Number(id);

  const machine = MACHINE_DATA.find((m) => m.id === idAsNumber);

  if (!machine) {
    return (
      <div>
        <h1>Machine not found</h1>
        <p>There is no machine with ID {id}.</p>
      </div>
    );
  }

  return (
    <div className="machine-details-container">
      <h1>Machine ID: {machine.id}</h1>
      <Link to={`/sites/${machine.site_id}`}>Site ID: {machine.site_id}</Link>
      <p>Machine status: {machine.status}</p>
      <p>Machine production status: {machine.prod_status}</p>
      <p>Location: {machine.locatie}</p>
      <p>Info: {machine.info}</p>
      <p>Uptime: {machine.uptime} hours</p>
      <p>Days since maintenance: {machine.dagenSindsOnderhoud}</p>
      <p>Next maintenance: {new Date(machine.volgendOnderhoud).toLocaleDateString()}</p>
      <p>Technician ID: {machine.technieker_id}</p>
      <Link to={`/machines/edit/${machine.id}`} className="btn">
        <IoPencilOutline /> Edit
      </Link>
    </div>
  );
};

export default MachineDetailBig;
