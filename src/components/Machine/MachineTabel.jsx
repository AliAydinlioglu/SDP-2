export default function MachineTabel({ machines }) {
  return (
    <div>
      <h2>Machines</h2>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>Machine ID</th>
            <th>Site ID</th>
            <th>Status</th>
            <th>Production Status</th>
          </tr>
        </thead>
        <tbody>
          {machines.map((machine) => (
            <tr key={machine.id}>
              <td>{machine.id}</td>
              <td>{machine.site_id}</td>
              <td>{machine.status}</td>
              <td>{machine.prod_status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}