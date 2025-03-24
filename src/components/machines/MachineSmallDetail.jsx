import { useParams, useOutletContext } from 'react-router-dom';

const MachineSmallDetail = () => {
  const { machineId } = useParams();
  const { machines } = useOutletContext();
  const machine = machines.find((m) => m.id === Number(machineId));

  if (!machine) {
    return (
      <div>
        <h1>Machine not found</h1>
      </div>
    );
  }

  return (
    <div className="machine-details-container w-100">
      <h1>Machine id: {machine.id}</h1>
      <p>Machine state: {machine.status}</p>
      <p>Machine production state: {machine.prod_status}</p>
      <a href={`/machines/${machineId}`}>zie meer</a>
    </div>
  );
};

export default MachineSmallDetail;
