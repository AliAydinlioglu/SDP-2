import { useParams } from 'react-router';
import { getById } from '../../api/index';
import useSWR from 'swr';
import AsyncData from '../../components/AsyncData';
import { IoPencilOutline } from 'react-icons/io5';
import { Link } from 'react-router-dom';

export default function User(){
  
  const { id } = useParams();
  const { data: user, error: userError, isLoading: userLoading } = useSWR(id ? `users/${id}` : null, getById);

  if (!user) {
    return (
      <div className="user-not-found">
        <h2>User Not Found</h2>
        <p>The user with ID {id} does not exist.</p>
      </div>
    );
  }
  
  return (
    <div className="user-details-container">
      <AsyncData loading={userLoading} error={userError}>
        <div className="user-details">
          <div className="user-header flex-row">
            <h2>User Details</h2>
            <Link to={`/users/edit/${id}`} className='btn btn-light'>
              <IoPencilOutline />
            </Link>
          </div>
          <div className="user-info">
            <p><strong>ID:</strong> {user.id}</p>
            <p><strong>Name:</strong> {user.voornaam} {user.achternaam}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>Phone:</strong> {user.gsm_nr}</p>
            <p><strong>Birth Date:</strong> {user.geboorteDatum}</p>
            <p><strong>Address:</strong> {user.straat} {user.huis_nr}, {user.stad}, {user.postcode}, {user.land}</p>
            <p><strong>Role:</strong> {user.rol}</p>
          </div>
        </div>  
      </AsyncData>
    </div>
  );
}