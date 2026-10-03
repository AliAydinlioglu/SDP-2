import { useState } from 'react';
import { MELDING_DATA } from '../api/mock_data';
import { FaSort } from 'react-icons/fa';

export default function Notifications() {
  const [meldingen, setMeldingen] = useState(MELDING_DATA);
  const [sortState, setSortState] = useState(0);
  const [sortOrder, setSortOrder] = useState('asc');
  const states = ['new', 'unread', 'read'];

  const sortMeldingenByState = () => {
    const sortedMeldingen = [...meldingen].sort((a, b) => {
      if (a.state === states[sortState]) return -1;
      if (b.state === states[sortState]) return 1;
      return 0;
    });
    setMeldingen(sortedMeldingen);
    setSortState((sortState + 1) % states.length);
  };

  const sortMeldingenByDatum = () => {
    const sortedMeldingen = [...meldingen].sort((a, b) => {
      if (sortOrder === 'asc') {
        return new Date(a.datum) - new Date(b.datum);
      } else {
        return new Date(b.datum) - new Date(a.datum);
      }
    });
    setMeldingen(sortedMeldingen);
    setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
  };

  return (
    <div className="meldingen-container">
      <h1>Notifications</h1>
      <table className="meldingen-table">
        <thead>
          <tr>
            <th onClick={sortMeldingenByDatum} style={{ cursor: 'pointer' }}>
              Date <FaSort />
            </th>
            <th>Type</th>
            <th>Description</th>
            <th onClick={sortMeldingenByState} style={{ cursor: 'pointer' }}>
              State <FaSort />
            </th>
          </tr>
        </thead>
        <tbody>
          {meldingen.map((melding) => (
            <tr key={melding.id}>
              <td>{melding.datum}</td>
              <td>{melding.type}</td>
              <td className="beschrijving">{melding.beschrijving}</td>
              <td>{melding.state}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}