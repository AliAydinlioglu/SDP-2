import { useNavigate } from 'react-router-dom';

const MachineRow = ({ id, site_id, state, prod_state }) => {
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
        {site_id}
      </td>
      <td>
        {state}
      </td>
      <td>
        {prod_state}
      </td>
    </tr>
  );
};

export default MachineRow;