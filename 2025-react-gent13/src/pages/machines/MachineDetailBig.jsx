import { useNavigate, useParams } from 'react-router-dom';
import useSWR from 'swr';
import useSWRMutation from 'swr/mutation';
import { getById, save } from '../../api';
import AsyncData from '../../components/AsyncData';
import './MachineDetailBig.css';

const MachineDetailBig = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  const { data: machine, error: machineError, isLoading: machineLoading } = 
    useSWR(id ? `machines/${id}` : null, getById);
  const { data: maintenance, error: maintenanceError, isLoading: maintenanceLoading } = 
    useSWR(id ? `onderhoud/${id}` : null, getById);
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
      <AsyncData loading={machineLoading || maintenanceLoading} error={machineError || maintenanceError || deleteError}>
        <div className="machine-info">
          <h1 data-cy="machine-id">Machine id: {machine.id}</h1>
          <p data-cy="machine-status">Machine status: {machine.status}</p>
          <p data-cy="machine-prod-status">Machine productie status: {machine.prod_status}</p>
          <p data-cy="machine-locatie">Locatie: {machine.locatie}</p>
          <p data-cy="machine-info">Info: {machine.info}</p>
          <p data-cy="machine-uptime">Uptime: {machine.uptime} hours</p>
          <p data-cy="machine-dagen-sinds-onderhoud">Dagen sinds onderhoud: {machine.dagenSindsOnderhoud}</p>
          <p data-cy="machine-volgend-onderhoud">
            Volgend onderhoud: {new Date(machine.volgendOnderhoud).toLocaleDateString()}</p>
          <button type="button" 
            className="btn btn-light" onClick={() => navigate(`/machines/edit/${machine.id}`)} data-cy="edit-btn">
            Edit
          </button>
          <button type="button" 
            className="btn btn-light" data-bs-toggle="modal" data-bs-target="#deleteModal" data-cy="delete-btn">
            Delete
          </button>
        </div>
      </AsyncData>
      <div className="machine-onderhoud">
        <h2 data-cy="last-maintenance-title">Last Maintenance</h2>
        {maintenance ? (
          <div className="maintenance-card" data-cy="maintenance-card">
            <div className="maintenance-card-content">
              <h3 data-cy="maintenance-type">{maintenance.onderhoud_type}</h3>
              <p data-cy="maintenance-date">Date: {new Date(maintenance.datum).toLocaleDateString()}</p>
              <p data-cy="maintenance-status">State: {maintenance.status}</p>
              <div>
                <label>Opmerking:</label>
                <div className="maintenance-opmerking" data-cy="maintenance-opmerking">
                  {maintenance.opmerkingen}
                </div>
              </div>
            </div>
          </div>
        ) : (
          <p data-cy="no-maintenance-data">No maintenance data available.</p>
        )}
        <div>
          <button className="btn btn-light" 
            onClick={() => navigate(`/maintenances?machine_id=${machine.id}`)} data-cy="view-all-maintenance-btn">
            View All Maintenances
          </button>
          <button className="btn btn-light" 
            onClick={() => navigate(`/maintenances/add?machine_id=${machine.id}`, {replace: true})} 
            data-cy="add-maintenance-btn">
            Add Maintenance
          </button>
        </div>
      </div>

      <div className="modal fade" id="deleteModal" tabIndex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">
              <h1 className="modal-title fs-5" id="deleteModalLabel" 
                data-cy="delete-modal-title">Delete Machine {id}</h1>
              <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div className="modal-body" data-cy="delete-modal-body">
              Are you sure you want to delete this machine?
              {deleteError && <div className="alert alert-danger">{deleteError.message}</div>}
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-danger" data-bs-dismiss="modal" 
                onClick={handleDelete} data-cy="confirm-delete-btn">
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