import { useState, useEffect } from 'react';
import MachineTabelBig from '../../components/machines/MachineTabelBig';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/auth';
import useSWR from 'swr';
import { getAll } from '../../api';
import AsyncData from '../../components/AsyncData';

const MachinesList = () => {
  const {
    data: data = [],
    loading: machinesLoading,
    error: machinesError,
  } = useSWR('/machines', getAll);
  const [machines, setMachines] = useState(data);
  const [sortOrderId, setSortOrderId] = useState('desc');
  const [sortOrderSiteId, setSortOrderSiteId] = useState('asc');
  const [text, setText] = useState('');
  const [stateFilter, setStateFilter] = useState('');
  const [productionStateFilter, setProductionStateFilter] = useState('');

  const {user} = useAuth();
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

    const sortedMachines = [...machines].sort((a, b) => {
      if (sortOrder === 'asc') {
        return a[prop] - b[prop];
      } else {
        return b[prop] - a[prop];
      }
    });
    setMachines(sortedMachines);
    setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
  };

  useEffect(() => {
    const filteredMachines = data.filter((m) => {
      
      return (
        m.id.toString().includes(text.toLowerCase()) &&
        (stateFilter === '' || m.status === stateFilter) &&
        (productionStateFilter === '' || m.prod_status === productionStateFilter)
      );
    });
    setMachines(filteredMachines);
  }, [text, stateFilter, productionStateFilter, data]);

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
            <Link to='/machines/add' className='btn btn-primary float-end'>
              Add machine
            </Link>
          )}
        </div>
      </div>
      <AsyncData loading={machinesLoading} error={machinesError}>
        <MachineTabelBig machines={machines} sortMachines={sortMachinesByProp} />
      </AsyncData>
    </div>
  );
};

export default MachinesList;