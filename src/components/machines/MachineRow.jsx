import { useNavigate } from 'react-router-dom';

const MachineRow = ({ id, site, info, status, prod_status }) => {
  const navigate = useNavigate();

  const handleRowClick = (id) => {
    navigate(`/machines/${id}`);
  };

  return (
    <tr key={id} onClick={
      () => handleRowClick(id)} style={{ cursor: 'pointer' }}>
      <td>
        {id}
      </td>
      <td>
        {site.naam} {/* Changed to display site name */}
      </td>
      <td>
        {info}
      </td>
      <td>
        {status}
      </td>
      <td>
        {prod_status}
      </td>
    </tr>
  );
};

export default MachineRow;