import { useNavigate } from 'react-router-dom';
import { memo } from 'react';

const MachineRow = memo(({ id, site, info, status, prod_status }) => {
  const navigate = useNavigate();

  const handleRowClick = (id) => {
    navigate(`/machines/${id}`);
  };

  return (
    <tr key={id} onClick={() => handleRowClick(id)} style={{ cursor: 'pointer' }}>
      <td>{id}</td>
      <td>{site.naam}</td> {/* Changed to display site name */}
      <td>{info}</td>
      <td>{status}</td>
      <td>{prod_status}</td>
    </tr>
  );
});

MachineRow.displayName = 'MachineRow';

export default MachineRow;