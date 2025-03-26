import { useState, useEffect } from 'react';
import SiteCards from '../../components/sites/SiteCards';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/auth';
import useSWR from 'swr';
import { getAll } from '../../api';
import AsyncData from '../../components/AsyncData';

const SitesList = () => {
  const {
    data: data = [],
    loading: sitesLoading,
    error: sitesError,
  } = useSWR('/sites', getAll);
  const [text, setText] = useState('');
  const [sites, setSites] = useState(data);

  const {user} = useAuth();
  const isAdmin = user?.rol === 'ADMINISTRATOR';

  useEffect(() => {
    const filteredSites = data.filter((s) => {
      return s.naam.toLowerCase().includes(text.toLowerCase());
    });
    setSites(filteredSites);
  }, [text, data]);

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
        <AsyncData loading={sitesLoading} error={sitesError}>
          <SiteCards sites={sites} />
        </AsyncData>
      </div>
    </div>
  );
};

export default SitesList;