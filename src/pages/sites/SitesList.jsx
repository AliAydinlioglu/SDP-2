import { SITE_DATA } from '../../api/mock_data';
import SiteCards from '../../components/sites/SiteCards';

const SitesList = () => {
  const sites = SITE_DATA;
  return (
    <div className='grid mt-3'>
      <SiteCards sites={sites} />
    </div>
  );
};

export default SitesList;