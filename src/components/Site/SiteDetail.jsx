export default function SiteDetail({site}){
  return (
    <div>
      <h1>Site Detail</h1>
      <p>Name: {site.name}</p>
      <p>Manager: {site.manager}</p>
      <p>Machine Count: {site.machineCount}</p>
    </div>
  );
}