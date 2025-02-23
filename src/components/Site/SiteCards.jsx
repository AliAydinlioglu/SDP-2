import SITE_DATA from '../../api/mock_data';
export default function SiteCards(){
  const sites = SITE_DATA;
  console.log(sites);
  
  return (
    <div className='grid'>
      <div >
        {sites
          .map((s) => (
            <div className="col mb-2" key={s.id}>
              <div className="card">
                <div className="card-body">
                  <h5 className="card-title">{s.name}</h5>
                  <p className="card-text">{s.manager}</p>
                  <p className="card-text">{s.machineCount}</p>
                </div>
              </div>
            </div>
          ))}
      </div>
    </div>
  );
}