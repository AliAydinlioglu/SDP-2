import { FaBell } from 'react-icons/fa';
import { MELDING_DATA } from '../api/mock_data';

export default function Meldingen() {

  const meldingen = MELDING_DATA.slice(-5);

  return (
    <div className="dropdown me-5">
      <FaBell className="bell-icon" type="button" 
        data-bs-toggle="dropdown" aria-expanded="false" data-bs-offset="10,20"/>
      <ul className="dropdown-menu dropdown-menu-end ">
        {meldingen.map((m)=> (
          <li className='dropdown-list' key={m.id}>
            <a className="dropdown-item text-wrap" href="#">{m.beschrijving}</a>
          </li>
        ))}
        <li><hr className="dropdown-divider" /></li>
        <li className="text-center"><button >Alle meldingen</button></li>
      </ul>
    </div>
  );
}
