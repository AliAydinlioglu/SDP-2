import { SITE_DATA } from '../../api/mock_data';
import SiteCard from '../../components/sites/SiteCards';

const SitesList = () => {
  const sites = SITE_DATA;
  return (
    <div className='grid mt-3'>
      <div className='row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xxl-4 g-3'>
        {sites
          .sort((a, b) =>
            a.name.toUpperCase().localeCompare(b.name.toUpperCase()),
          )
          .map((s) => (
            <div className='col' key={s.id}>
              <SiteCard {...s} />
            </div>
          ))}
      </div>
    </div>
  );
};

export default SitesList;