import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import ReadingListService from './reading-list.service';
import { DATE_FORMAT, DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { ReadingList } from '@/shared/model/reading-list.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('ReadingList Service', () => {
    let service: ReadingListService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new ReadingListService();
      currentDate = new Date();
      elemDefault = new ReadingList(123, 'AAAAAAA', 'AAAAAAA', currentDate, currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          startDate: dayjs(currentDate).format(DATE_FORMAT),
          endDate: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a ReadingList', async () => {
        const returnedFromService = {
          id: 123,
          startDate: dayjs(currentDate).format(DATE_FORMAT),
          endDate: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { startDate: currentDate, endDate: currentDate, createdAt: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a ReadingList', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a ReadingList', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          status: 'BBBBBB',
          startDate: dayjs(currentDate).format(DATE_FORMAT),
          endDate: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = { startDate: currentDate, endDate: currentDate, createdAt: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a ReadingList', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a ReadingList', async () => {
        const patchObject = {
          title: 'BBBBBB',
          startDate: dayjs(currentDate).format(DATE_FORMAT),
          endDate: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...new ReadingList(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { startDate: currentDate, endDate: currentDate, createdAt: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a ReadingList', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of ReadingList', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          status: 'BBBBBB',
          startDate: dayjs(currentDate).format(DATE_FORMAT),
          endDate: dayjs(currentDate).format(DATE_FORMAT),
          createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { startDate: currentDate, endDate: currentDate, createdAt: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of ReadingList', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a ReadingList', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a ReadingList', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
