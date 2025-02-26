import { MACHINE_DATA } from '../../api/mock_data';

export default function SiteDetail({ site }) {
  const machineCount = MACHINE_DATA.filter((machine) => machine.site_id === site.id).length;

  return (
    <div>
      <h1>Site Detail</h1>
      <p>Name: {site.name}</p>
      <p>Manager: {site.manager}</p>
      <p>Address: {site.address}</p>
      <p>Number of Machines: {machineCount}</p>
    </div>
  );
}