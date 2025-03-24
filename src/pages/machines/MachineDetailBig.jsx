import { useParams } from 'react-router-dom';
import { MACHINE_DATA } from '../../api/mock_data';
import { Link } from 'react-router-dom';
import useSWR from 'swr';
import { getById } from '../../api';
import AsyncData from '../../components/AsyncData';
import './MachineDetailBig.css'; // Import the new CSS file

const MachineDetailBig = () => {
  const { id } = useParams();
  const idAsNumber = Number(id);

  const machine = MACHINE_DATA.find((m) => m.id === idAsNumber);
  // const {
  //   data: machine,
  //   error: machineError,
  //   loading: machineLoading,
  // } = useSWR(idAsNumber ? `machines/${idAsNumber}` : null, getById );

  if (!machine) {
    return (
      <div>
        <h1>Machine not found</h1>
        <p>There is no machine with id {id}.</p>
      </div>
    );
  }

  return (
    <div className="machine-detail-container">
      <div className="machine-info">
        {/* <AsyncData loading={machineLoading} error={machineError}> */}
        <h1>Machine id: {machine.id}</h1>
        <p>Machine status: {machine.status}</p>
        <p>Machine productie status: {machine.prod_status}</p>
        <p>Locatie: {machine.locatie}</p>
        <p>Info: {machine.info}</p>
        <p>Uptime: {machine.uptime} hours</p>
        <p>Dagen sinds onderhoud: {machine.dagenSindsOnderhoud}</p>
        <p>Volgend onderhoud: {new Date(machine.volgendOnderhoud).toLocaleDateString()}</p>
        <Link to={`/machines/edit/${machine.id}`} className='btn btn-light'>
          Edit
        </Link>
        {/* </AsyncData> */}
      </div>
      <div className="machine-onderhoud">
        {/* Placeholder for future onderhoud functionality */}
        <h2>Onderhoud</h2>
        <p>Details about onderhoud will be displayed here.</p>
      </div>
    </div>
  );
};

export default MachineDetailBig;
