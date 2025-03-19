import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import FinanceRecord from './finance-record.vue';
import FinanceRecordService from './finance-record.service';
import AlertService from '@/shared/alert/alert.service';

type FinanceRecordComponentType = InstanceType<typeof FinanceRecord>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('FinanceRecord Management Component', () => {
    let financeRecordServiceStub: SinonStubbedInstance<FinanceRecordService>;
    let mountOptions: MountingOptions<FinanceRecordComponentType>['global'];

    beforeEach(() => {
      financeRecordServiceStub = sinon.createStubInstance<FinanceRecordService>(FinanceRecordService);
      financeRecordServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          financeRecordService: () => financeRecordServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        financeRecordServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(FinanceRecord, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(financeRecordServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.financeRecords[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(FinanceRecord, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(financeRecordServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: FinanceRecordComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(FinanceRecord, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        financeRecordServiceStub.retrieve.reset();
        financeRecordServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        financeRecordServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(financeRecordServiceStub.retrieve.called).toBeTruthy();
        expect(comp.financeRecords[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(financeRecordServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        financeRecordServiceStub.retrieve.reset();
        financeRecordServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(financeRecordServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.financeRecords[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(financeRecordServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        financeRecordServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeFinanceRecord();
        await comp.$nextTick(); // clear components

        // THEN
        expect(financeRecordServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(financeRecordServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
