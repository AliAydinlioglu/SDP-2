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
    <div>
      <h1>Machine id: {machine.id}</h1>
      <p>Machine state: {machine.state}</p>
      <p>Machine production state: {machine.prod_state}</p>
    </div>
  );
};

export default MachineSmallDetail;
