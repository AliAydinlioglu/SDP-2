import { useNavigate, useParams } from 'react-router';
import { getById, save } from '../../api/index';
import useSWR from 'swr';
import AsyncData from '../../components/AsyncData';

import useSWRMutation from 'swr/mutation';

export default function User(){

  const navigate = useNavigate();
  
  const { id } = useParams();
  const { data: user, error: userError, isLoading: userLoading } = useSWR(id ? `users/${id}` : null, getById);
  const {trigger: deleteUser, error: deleteError} = useSWRMutation('users', save);

  const handleDelete = async () => {
    await deleteUser({
      id: id,
      actief: false,
    });

    navigate('/users');

  };

  if (!user) {
    return (
      <div className="user-not-found">
        <h2>User Not Found</h2>
        <p>The user with ID {id} does not exist.</p>
      </div>
    );
  }
  
  return (
    <>
      <div className="user-details-container">
        <AsyncData loading={userLoading} error={userError || deleteError}>
          <div className="user-details">
            <div className="user-header">
              <h2 data-cy="user-details-title">User Details</h2>
            </div>
            <div className="user-info">
              <p data-cy="user-id"><strong>ID:</strong> {user.id}</p>
              <p data-cy="user-name"><strong>Name:</strong> {user.voornaam} {user.achternaam}</p>
              <p data-cy="user-email"><strong>Email:</strong> {user.email}</p>
              <p data-cy="user-phone"><strong>Phone:</strong> {user.gsm_nr}</p>
              <p data-cy="user-birth-date"><strong>Birth Date:</strong> {user.geboorteDatum}</p>
              <p data-cy="user-address"><strong>Address:
              </strong> {user.straat} {user.huis_nr}, {user.stad}, {user.postcode}, {user.land}</p>
              <p data-cy="user-role"><strong>Role:</strong> {user.rol}</p>
            </div>
            <div className="action-buttons">
              <button type='button' className='btn btn-danger' 
                data-cy="edit-btn" onClick={() => navigate(`/users/edit/${id}`)}>
                Edit
              </button>
              <button type="button" className="btn btn-danger" 
                data-cy="delete-btn" data-bs-toggle="modal" data-bs-target="#exampleModal">
                Delete
              </button>
            </div>
          </div>  
        </AsyncData>
      </div>
      <div className="modal fade" id="exampleModal" tabIndex="-1" 
        aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">
              <h1 className="modal-title fs-5" id="exampleModalLabel" data-cy="delete-modal-title">Delete User {id}</h1>
              <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div className="modal-body" data-cy="delete-modal-body">
              Are you sure you want to delete this user?
              {deleteError && <div className="alert alert-danger">{deleteError.message}</div>}
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
              <button type="button" className="btn btn-danger" 
                data-bs-dismiss="modal" onClick={handleDelete} data-cy="confirm-delete-btn">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}