import { useNavigate, useParams } from 'react-router-dom';
import useSWR from 'swr';
import useSWRMutation from 'swr/mutation';
import { getById, save } from '../../api';
import AsyncData from '../../components/AsyncData';
import './MachineDetailBig.css';

const MachineDetailBig = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const { data: machine, error: machineError, isLoading: machineLoading } = 
    useSWR(id ? `machines/${id}` : null, getById);
  const { trigger: deleteMachine, error: deleteError } = useSWRMutation('machines', save);

  const handleDelete = async () => {
    await deleteMachine({
      id: id,
      actief: false,
    });

    navigate('/machines');
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
      <AsyncData loading={machineLoading} error={machineError || deleteError}>
        <div className="machine-info">
          <h1>Machine id: {machine.id}</h1>
          <p>Machine status: {machine.status}</p>
          <p>Machine productie status: {machine.prod_status}</p>
          <p>Locatie: {machine.locatie}</p>
          <p>Info: {machine.info}</p>
          <p>Uptime: {machine.uptime} hours</p>
          <p>Dagen sinds onderhoud: {machine.dagenSindsOnderhoud}</p>
          <p>Volgend onderhoud: {new Date(machine.volgendOnderhoud).toLocaleDateString()}</p>
          <button type="button" className="btn btn-light" onClick={() => navigate(`/machines/edit/${machine.id}`)}>
            Edit
          </button>
          <button type="button" className="btn btn-light" data-bs-toggle="modal" data-bs-target="#deleteModal">
            Delete
          </button>
        </div>
      </AsyncData>
      <div className="machine-onderhoud">
        {/* Placeholder for future onderhoud functionality */}
        <h2>Onderhoud</h2>
        <p>Details about onderhoud will be displayed here.</p>
      </div>

      <div className="modal fade" id="deleteModal" tabIndex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">
              <h1 className="modal-title fs-5" id="deleteModalLabel">Delete Machine {id}</h1>
              <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div className="modal-body">
              Are you sure you want to delete this machine?
              {deleteError && <div className="alert alert-danger">{deleteError.message}</div>}
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-danger" data-bs-dismiss="modal" onClick={handleDelete}>
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MachineDetailBig;