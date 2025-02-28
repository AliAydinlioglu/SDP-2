import { FaBell } from 'react-icons/fa';
import { MELDING_DATA } from '../api/mock_data';

export default function Meldingen() {

  const meldingen = MELDING_DATA;

  return (
    <div className="dropdown me-5">
      <FaBell className="bell-icon" type="button" data-bs-toggle="dropdown" aria-expanded="false"/>
      <ul className="dropdown-menu dropdown-menu-end dropdown-menu-lg-start">
        {meldingen.map((m)=> (
          <li key={m.id}><a className="dropdown-item text-wrap border-bottom" href="#">{m.beschrijving}</a></li>
        ))}
      </ul>
    </div>
  );
}
