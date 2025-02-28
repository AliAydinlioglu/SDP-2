import { useNavigate } from "react-router-dom";

const MachineRow = ({ id, site_id, status, prod_status }) => {
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
        {status}
      </td>
      <td>
        {prod_status}
      </td>
    </tr>
  );
};

export default MachineRow;