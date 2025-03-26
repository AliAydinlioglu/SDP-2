import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useSWR from 'swr';
import { getAll } from '../../api';
import MachineTabelBig from '../../components/machines/MachineTabelBig';
import AsyncData from '../../components/AsyncData';
import { useAuth } from '../../context/auth';

const MachinesList = () => {
  const { data: machines = [], loading: machinesLoading, error: machinesError } = useSWR('/machines', getAll);
  const [text, setText] = useState('');
  const [stateFilter, setStateFilter] = useState('');
  const [productionStateFilter, setProductionStateFilter] = useState('');
  const [filteredMachines, setFilteredMachines] = useState([]);
  const [sortOrderId, setSortOrderId] = useState('asc');
  const [sortOrderSiteId, setSortOrderSiteId] = useState('asc');
  const navigate = useNavigate();

  const { user } = useAuth();
  const isAdmin = user?.rol === 'ADMINISTRATOR';

  const sortMachinesByProp = (prop) => {
    let sortOrder, setSortOrder;
    if (prop === 'id') {
      sortOrder = sortOrderId;
      setSortOrder = setSortOrderId;
    } else if (prop === 'site_id') {
      sortOrder = sortOrderSiteId;
      setSortOrder = setSortOrderSiteId;
    }

    const sortedMachines = [...filteredMachines].sort((a, b) => {
      if (sortOrder === 'asc') {
        return a[prop] - b[prop];
      } else {
        return b[prop] - a[prop];
      }
    });
    setFilteredMachines(sortedMachines);
    setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
  };

  useEffect(() => {
    const filterMachines = () => {
      const filtered = machines.filter((m) => {
        return (
          m.id.toString().includes(text.toLowerCase()) &&
          (stateFilter === '' || m.status === stateFilter) &&
          (productionStateFilter === '' || m.prod_status === productionStateFilter)
        );
      });
      setFilteredMachines(filtered);
    };

    filterMachines();
  }, [machines, text, stateFilter, productionStateFilter]);

  const handleRowClick = (id) => {
    navigate(`/machines/${id}`);
  };

  return (
    <div className='machine-tabel-big-container'>
      <div className='input-group'>
        <input
          type='search'
          id='search'
          className='search-bar'
          placeholder='Search'
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
        <select
          value={stateFilter}
          onChange={(e) => setStateFilter(e.target.value)}
          className='states'
        >
          <option value=''>All States</option>
          <option value='active'>Active</option>
          <option value='inactive'>Inactive</option>
        </select>
        <select
          value={productionStateFilter}
          onChange={(e) => setProductionStateFilter(e.target.value)}
          className='production-states'
        >
          <option value=''>All Production States</option>
          <option value='running'>Running</option>
          <option value='idle'>Idle</option>
          <option value='maintenance'>Maintenance</option>
        </select>

        <div className='clearfix'>
          {isAdmin && (
            <button
              className='btn btn-primary float-end'
              onClick={() => navigate('/machines/add')}
            >
              Add Machine
            </button>
          )}
        </div>
      </div>
      <AsyncData loading={machinesLoading} error={machinesError}>
        <MachineTabelBig machines={filteredMachines} sortMachines={sortMachinesByProp} onRowClick={handleRowClick} />
      </AsyncData>
    </div>
  );
};

export default MachinesList;