import useSWR from 'swr';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAll } from '../../api';
import AsyncData from '../../components/AsyncData';
import { ONDERHOUD_DATA } from '../../api/mock_data';
import { FaSort } from 'react-icons/fa';

export default function OnderhoudList() {
  const navigate = useNavigate();
  //   const { data: onderhouds = [], loading: onderhoudsLoading, error: onderhoudsError } = useSWR('/onderhouds', getAll);
  const [searchTerm, setSearchTerm] = useState('');
  const [onderhouds, setOnderhouds] = useState(ONDERHOUD_DATA);
  const [sortOrder, setSortOrder] = useState('asc');

  const filteredOnderhouds = onderhouds.filter((onderhoud) =>
    (onderhoud.id.toString().includes(searchTerm) ||
    onderhoud.name.toLowerCase().includes(searchTerm.toLowerCase())),
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

  return (
    <div>
      <div className='input-group'>
        <input
          className='user-search-bar'
          type="text"
          placeholder="Search by ID or name"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>
      {/* <AsyncData loading={onderhoudsLoading} error={onderhoudsError}> */}
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
      {/* </AsyncData> */}
    </div>
  );
}