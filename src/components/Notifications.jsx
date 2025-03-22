import { FaBell } from 'react-icons/fa';
import { useNavigate } from 'react-router';

export default function Notifications({meldingen}) {

  const navigate = useNavigate();
  const handleOnClick = () => {
    navigate('/notifications');
  };

  meldingen = meldingen.slice(-5);

  if(meldingen.length === 0) {
    return (
      <div className="dropdown me-5">
        <FaBell className="bell-icon" type="button" 
          data-bs-toggle="dropdown" aria-expanded="false" data-bs-offset="10,20"/>
        <ul className="dropdown-menu dropdown-menu-end ">
          <li className='dropdown-list'>
            <a className="dropdown-item disabled text-wrap d-flex align-items-center justify-content-between">
              No notifications
            </a>
          </li>
        </ul>
      </div>
    );
  }

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
