import { useParams, useOutletContext } from 'react-router-dom';

const MachineDetail = () => {
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
    <div>
      <h1>Machine id: {machine.id}</h1>
      <p>Machine status: {machine.status}</p>
      <p>Machine productie status: {machine.prod_status}</p>
    </div>
  );
};

export default MachineDetail;
