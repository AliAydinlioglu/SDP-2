import { useState, useEffect } from 'react';
import { SITE_DATA } from '../../api/mock_data';
import SiteCards from '../../components/sites/SiteCards';

const SitesList = () => {
  const [text, setText] = useState('');
  const [sites, setSites] = useState(SITE_DATA);

  useEffect(() => {
    const filteredSites = SITE_DATA.filter((s) => {
      return s.name.toLowerCase().includes(text.toLowerCase());
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
      </div>
      <div className='sites-cards-container'>
        <SiteCards sites={sites} />
      </div>
    </div>
  );
};

export default SitesList;