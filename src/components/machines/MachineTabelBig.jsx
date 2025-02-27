import MachineRow from "./MachineRow";

function MachineTabelBig({ machines }) {
  if (machines.length === 0) {
    return (
      <div className="alert alert-info">There are no machines available.</div>
    );
  }

  return (
    <div>
      <table className='table '>
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
            <MachineRow key={machine.id} {...machine} />
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default MachineTabelBig;
