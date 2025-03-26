import { useNavigate } from 'react-router-dom';
import { useCallback, memo } from 'react';

const MachineRow = memo(({ machine, onClick }) => (
  <tr key={machine.id} onClick={() => onClick(machine.id, machine.site_id)} style={{ cursor: 'pointer' }}>
    <td>{machine.id}</td>
    <td>{machine.site_id}</td>
    <td>{machine.status}</td>
    <td>{machine.prod_status}</td>
  </tr>
));

MachineRow.displayName = 'MachineRow';

export default function MachineTabelSmall({ machines }) {
  const navigate = useNavigate();

  const handleRowClick = useCallback((id, siteId) => {
    navigate(`/sites/${siteId}/machines/${id}`);
  }, [navigate]);

  return (
    <div>
      <h2>Machines</h2>
      <div className='small-table-machines-container'>
        <table className="table small-table-machines">
          <thead>
            <tr>
              <th>Machine ID</th>
              <th>Site ID</th>
              <th>State</th>
              <th>Production State</th>
            </tr>
          </thead>
          <tbody>
            {machines.map((machine) => (
              <MachineRow key={machine.id} machine={machine} onClick={handleRowClick} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}