import { Link } from 'react-router-dom';

export default function SiteCards({sites}) {
  console.log(sites);
  
  return (
    <div className="container">
      <div className="row">
        {sites.map((s) => (
          <Link to={`/sites/${s.id}`} key={s.id} >
            <div className="col-md-6 mb-2" key={s.id}>
              <div className="card m-2 h-100 w-100  mt-2 mb-2">
                <div className="card-body">
                  <h5 className="card-title">{s.name}</h5>
                  <p className="card-text">{s.manager}</p>
                  <p className="card-text">{s.machineCount}</p>
                </div>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}