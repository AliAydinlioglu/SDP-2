import useSWR from 'swr';
import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { getAll } from '../../api';
import AsyncData from '../../components/AsyncData';
import { FaSort } from 'react-icons/fa';

export default function OnderhoudList() {
  const {
    data: data =[],
    loading: onderhoudsLoading,
    error: onderhoudsError,
  } = useSWR('/onderhoud', getAll);
  const navigate = useNavigate();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const machineIdFromQuery = queryParams.get('machine_id') || '';

  const [searchTerm, setSearchTerm] = useState(machineIdFromQuery);
  const [onderhouds, setOnderhouds] = useState(data);
  const [sortOrder, setSortOrder] = useState('asc');

  const filteredOnderhouds = onderhouds.filter((onderhoud) =>
    onderhoud.machine_id.toString().includes(searchTerm),
  );

  const sortMeldingenByDatum = () => {
    const sortedOnderhouds = [...onderhouds].sort((a, b) => {
      if (sortOrder === 'asc') {
        return new Date(a.datum) - new Date(b.datum);
      } else {
        return new Date(b.datum) - new Date(a.datum);
      }
    });
    setOnderhouds(sortedOnderhouds);
    setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
  };

  const handleRowClick = (id) => {
    navigate(`/maintenances/${id}`);
  };

  const handleClearSearch = () => {
    setSearchTerm('');
  };

  return (
    <div>
      <div className='input-group'>
        <input
          className='user-search-bar'
          type="text"
          placeholder="Search by Machine ID"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        {searchTerm && (
          <button className='btn btn-light' onClick={handleClearSearch}>
            Done
          </button>
        )}
      </div>
      <AsyncData loading={onderhoudsLoading} error={onderhoudsError}>
        <div className="">
          <table className='user-table'>
            <thead>
              <tr>
                <th>ID </th>
                <th onClick={sortMeldingenByDatum} style={{ cursor: 'pointer' }}>
                  Date <FaSort />
                </th>
                <th>Machine</th>
                <th>State</th>
              </tr>
            </thead>
            <tbody>
              {filteredOnderhouds.map((onderhoud) => (
                <tr
                  key={onderhoud.id}
                  style={{ cursor: 'pointer' }}
                  onClick={() => handleRowClick(onderhoud.id)}
                >
                  <td>{onderhoud.id}</td>
                  <td>{onderhoud.datum}</td>
                  <td>{onderhoud.machine_id}</td>
                  <td>{onderhoud.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </AsyncData>
    </div>
  );
}