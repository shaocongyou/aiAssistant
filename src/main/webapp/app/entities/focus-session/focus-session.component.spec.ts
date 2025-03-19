import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import FocusSession from './focus-session.vue';
import FocusSessionService from './focus-session.service';
import AlertService from '@/shared/alert/alert.service';

type FocusSessionComponentType = InstanceType<typeof FocusSession>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('FocusSession Management Component', () => {
    let focusSessionServiceStub: SinonStubbedInstance<FocusSessionService>;
    let mountOptions: MountingOptions<FocusSessionComponentType>['global'];

    beforeEach(() => {
      focusSessionServiceStub = sinon.createStubInstance<FocusSessionService>(FocusSessionService);
      focusSessionServiceStub.retrieve.resolves({ headers: {} });

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
          focusSessionService: () => focusSessionServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        focusSessionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(FocusSession, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(focusSessionServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.focusSessions[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(FocusSession, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(focusSessionServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: FocusSessionComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(FocusSession, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        focusSessionServiceStub.retrieve.reset();
        focusSessionServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        focusSessionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(focusSessionServiceStub.retrieve.called).toBeTruthy();
        expect(comp.focusSessions[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(focusSessionServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        focusSessionServiceStub.retrieve.reset();
        focusSessionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(focusSessionServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.focusSessions[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(focusSessionServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        focusSessionServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeFocusSession();
        await comp.$nextTick(); // clear components

        // THEN
        expect(focusSessionServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(focusSessionServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
