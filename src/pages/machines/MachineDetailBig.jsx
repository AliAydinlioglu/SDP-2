import { useNavigate, useParams } from 'react-router-dom';
import { Link } from 'react-router-dom';
import useSWR, { mutate } from 'swr';
import { deleteById, getById, save } from '../../api';
import AsyncData from '../../components/AsyncData';
import './MachineDetailBig.css'; // Import the new CSS file
import useSWRMutation from 'swr/mutation';
import { ONDERHOUD_DATA } from '../../api/mock_data';

const MachineDetailBig = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const idAsNumber = Number(id);

  // const machine = MACHINE_DATA.find((m) => m.id === idAsNumber);
  const maintenance = ONDERHOUD_DATA.find((m) => m.machine_id === idAsNumber);
  const {
    data: machine,
    error: machineError,
    loading: machineLoading,
  } = useSWR(idAsNumber ? `machines/${idAsNumber}` : null, getById );

  const {
    trigger: deleteMachine,
    error: deleteError,
  } = useSWRMutation('machines', deleteById);
  const {
    trigger: saveMachine,
    error: saveError,
  } = useSWRMutation('machines', save);

  const handleDelete = async () =>{
    await deleteMachine(idAsNumber);
    navigate('/machines');
  };

  const handleStatusChange = async () => {
    const updatedMachine = {
      id: machine.id,
      prod_status: machine.prod_status === 'Active' ? 'Deactivated (manually)' : 'Active',
    };
    await saveMachine(updatedMachine);
    mutate(`machines/${idAsNumber}`); // Revalidate the SWR data
  };

  if (!machine) {
    return (
      <div>
        <h1>Machine not found</h1>
        <p>There is no machine with ID {id}.</p>
      </div>
    );
  }

  return (
    <div className="machine-detail-container">
      <div className="machine-info">
        <AsyncData loading={machineLoading} error={machineError || deleteError || saveError}>
          <h1>Machine id: {machine.id}</h1>
          <p>Machine state: {machine.status}</p>
          <p>Machine production state: {machine.prod_status}</p>
          <p>Location: {machine.locatie}</p>
          <p>Info: {machine.info}</p>
          <p>Uptime: {machine.uptime} hours</p>
          <p>Days since last maintenance: {machine.dagenSindsOnderhoud}</p>
          <p>Next maintenance: {new Date(machine.volgendOnderhoud).toLocaleDateString()}</p>
          <div>
            <Link to={`/machines/edit/${machine.id}`} className='btn btn-light'>
              Edit
            </Link>
            <button className='btn btn-light' onClick={handleDelete}>
              Delete
            </button>
            <button className='btn btn-light' onClick={handleStatusChange}>
              {machine.prod_status === 'Active' ? 'Stop' : 'Start'}
            </button>
          </div>
        </AsyncData>
      </div>
      <div className="machine-onderhoud">
        <h2>Last Maintenance</h2>
        {maintenance ? (
          <div className="maintenance-card">
            <div className="maintenance-card-content">
              <h3>{maintenance.onderhoud_type}</h3>
              <p>Date: {new Date(maintenance.datum).toLocaleDateString()}</p>
              <p>State: {maintenance.status}</p>
              <div>
                <label>Opmerking:</label>
                <div className="maintenance-opmerking">
                  {maintenance.opmerking}
                </div>
              </div>
            </div>
          </div>
        ) : (
          <p>No maintenance data available.</p>
        )}
        <div>
          <button className="btn btn-light" onClick={() => navigate(`/maintenances?machine_id=${machine.id}`)}>
            View All Maintenances
          </button>
          <button className="btn btn-light" 
            onClick={() => navigate(`/maintenances/add?machine_id=${machine.id}`, {replace: true})}>
            Add Maintenance
          </button>
        </div>
      </div>
    </div>
  );
};

export default MachineDetailBig;
