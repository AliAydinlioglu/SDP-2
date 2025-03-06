import { useNavigate } from 'react-router-dom';

export default function MachineTabelSmall({ machines }) {
  const navigate = useNavigate();

  const handleRowClick = (id, siteId) => {
    navigate(`/sites/${siteId}/machines/${id}`);
  };

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
              <tr key={machine.id} onClick={
                () => handleRowClick(machine.id, machine.site_id)} style={{ cursor: 'pointer' }}>
                <td>
                  {machine.id}
                </td>
                <td>
                  {machine.site_id}
                </td>
                <td>
                  {machine.state}
                </td>
                <td>
                  {machine.prod_state}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}