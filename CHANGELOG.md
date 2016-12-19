# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [0.2.0] - 2016-12-19
### Added
- Switched to [re-frame](https://github.com/Day8/re-frame) 0.9.0
- `::render-fn` key that allows to specify custom rendering function
- Styling of `<th>` element after sorting

### Removed
- `::th-classes` key from `columns-def` vector elements. Styling should happen outside of component

## 0.1.0 - 2016-12-15
### Added
- Initial DataTable implmenetation
- sorting
- pagination
- Styling of `<table>` element
