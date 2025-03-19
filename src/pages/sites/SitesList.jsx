import { useState, useEffect } from 'react';
import { SITE_DATA } from '../../api/mock_data';
import SiteCards from '../../components/sites/SiteCards';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/auth';

const SitesList = () => {
  const [text, setText] = useState('');
  const [sites, setSites] = useState(SITE_DATA);

  const {user} = useAuth();
  const isAdmin = user?.rol === 'ADMINISTRATOR';

  useEffect(() => {
    const filteredSites = SITE_DATA.filter((s) => {
      return s.naam.toLowerCase().includes(text.toLowerCase());
    });
    setSites(filteredSites);
  }, [text]);

  return (
    <div className='sites-list-container'>
      <div className='input-group'>
        <input
          type='search'
          id='search'
          className='site-search-bar'
          placeholder='Search'
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
        {isAdmin && (
          <Link to='/sites/add' className='btn btn-primary'>
            Add site
          </Link>
        )}
      </div>
      <div className='sites-cards-container'>
        <SiteCards sites={sites} />
      </div>
    </div>
  );
};

export default SitesList;