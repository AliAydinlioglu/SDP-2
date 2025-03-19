import MachineRow from './MachineRow';
import { FaSort } from 'react-icons/fa';

function MachineTabelBig({ machines, sortMachines }) {
  
  if (machines.length === 0) {
    return (
      <div className="alert alert-info">There are no machines available.</div>
    );
  }

  return (
    <div className="machine-tabel-big-container">
      <table className='machine-tabel-big'>
        <thead>
          <tr>
            <th onClick={() => sortMachines('id')} style={{ cursor: 'pointer' }}>Machine ID <FaSort /></th>
            <th onClick={() => sortMachines('site_id')} style={{ cursor: 'pointer' }}>Site ID <FaSort /></th>
            <th>Info</th>
            <th>State</th>
            <th>Production State</th>
          </tr>
        </thead>
        <tbody>
          {machines.map((machine) => (
            <MachineRow key={machine.id} {...machine} />
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default MachineTabelBig;
