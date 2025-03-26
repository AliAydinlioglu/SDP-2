import { useParams, useOutletContext } from 'react-router-dom';

const MachineSmallDetail = () => {
  const { machineId } = useParams();
  const { machines } = useOutletContext();
  const machine = machines.find(({ id }) => id === Number(machineId));

  if (!machine) {
    return (
      <div>
        <h1>Machine not found</h1>
      </div>
    );
  }

  const { id, status, prod_status } = machine;

  return (
    <div className="machine-details-container w-100">
      <h1>Machine id: {id}</h1>
      <p>Machine state: {status}</p>
      <p>Machine production state: {prod_status}</p>
      <a href={`/machines/${machineId}`}>zie meer</a>
    </div>
  );
};

export default MachineSmallDetail;
