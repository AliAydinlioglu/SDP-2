import { FaBell } from 'react-icons/fa';
import { MELDING_DATA } from '../api/mock_data';
import { useNavigate } from 'react-router';

export default function Notifications() {

  const navigate = useNavigate();
  const handleOnClick = () => {
    navigate('/notifications');
  };

  const meldingen = MELDING_DATA.slice(-5);

  return (
    <div className="dropdown me-5">
      <FaBell className="bell-icon" type="button" 
        data-bs-toggle="dropdown" aria-expanded="false" data-bs-offset="10,20"/>
      <ul className="dropdown-menu dropdown-menu-end ">
        {meldingen.map((m)=> (
          <li className='dropdown-list' key={m.id}>
            <a className="dropdown-item text-wrap d-flex align-items-center justify-content-between" href="#">
              {m.beschrijving}
              {m.state === 'nieuw' && <span className="red-dot"></span>}
            </a>
          </li>
        ))}
        <li><hr className="dropdown-divider" /></li>
        <li className="text-center"><button onClick={handleOnClick} >All notifications</button></li>
      </ul>
    </div>
  );
}
